package sample.cluster.transformation

final case class JobFailed(reason: String, job: TransformationJob) //任务失败相应原因