@Library('jenkins-sharedlib@master')
import sharedlib.JenkinsfileUtil

def utils = new JenkinsfileUtil(steps, this)
/* Project 4 letters. */
def project = 'NFMC'
/* Mail configuration*/
// If recipients is null the mail is sent to the person who start the job
// The mails should be separated by commas(',')
def recipients = ""
try {
    node {
        stage('Preparation') {
            utils.notifyByMail('START', recipients)
            checkout scm
            env.project = "${project}"
            utils.prepare()
            utils.setAndroidGradleVersion("ANDROID_31_GRADLE61")
        }
        stage('Start Release') {
            utils.startReleaseAndroidGradle("networking:assembleCertProdRelease", false, false)
        }
        stage('Results') {
            utils.saveResultGradleAndroid('aar')
        }
        stage('Post Execution') {
            utils.executePostExecutionTasks()
            utils.notifyByMail('SUCCESS', recipients)
        }
    }
} catch (Exception e) {
    node {
        utils.executeOnErrorExecutionTasks()
        utils.notifyByMail('FAIL', recipients)
        throw e
    }
}