def build_Project() {
    echo 'Building project with Maven...'
    sh 'mvn clean package'
}
def chechout_code() {
    echo 'Checking out code...'
    checkout scm
}
def clean_up() {
    echo 'Cleaning up...'
    sh 'pkill -f "mvn spring-boot:run" || true'
}
def cleanup_Workspace() {
    echo 'Cleaning up the workspace...'
    deleteDir() // Deletes all files in the current workspace
}
def run_Application() {
    echo 'Running Spring Boot application...'
    sh 'nohup mvn spring-boot:run &'
    sleep(time: 15, unit: 'SECONDS')

    def public_Ip = sh(script: "curl -s https://checkip.amazonaws.com", returnStdout: true).trim()
    echo "The application is running and accessible at: http://${publicIp}:8080"
}
def setup_Java() {
    echo 'Setting up Java 17...'
    sh 'sudo apt update'
    sh 'sudo apt install -y openjdk-17-jdk'
}
def setup_Maven() {
    echo 'Setting up Maven...'
    sh 'sudo apt install -y maven'
}
def stop_Application() {
    echo 'Gracefully stopping the Spring Boot application...'
    sh 'mvn spring-boot:stop'
}
def tag_Build(String tagName, String message = 'Build tagging') {
    echo "Tagging the build with tag: ${tagName}"
    
    // Tagging the current commit in Git
    sh """
        git tag -a ${tagName} -m '${message}'
        git push origin ${tagName}
    """
}
def upload_Artifact(String artifactPath) {
    echo 'Uploading artifact...'
    archiveArtifacts artifacts: artifactPath, allowEmptyArchive: true
}
def validate_App() {
    echo 'Validating that the app is running...'
    def response = sh(script: 'curl --write-out "%{http_code}" --silent --output /dev/null http://localhost:8080', returnStdout: true).trim()
    if (response == "200") {
        echo 'The app is running successfully!'
    } else {
        echo "The app failed to start. HTTP response code: ${response}"
        error("The app did not start correctly!")
    }
}
