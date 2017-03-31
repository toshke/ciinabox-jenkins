import static ciinabox.JobHelper.*

//if(folder != null && folder != "") {
//  folder("$folder") {
//    description("$folder")
//  }
//  jobName = "$folder/$jobName"
//} else {
//  jobName = "$jobName"
//}
def job = job('test123')
defaults(job,jm.getParameters())
