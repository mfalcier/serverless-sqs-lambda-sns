# serverless-sqs-lambda-sns
An AWS Lambda written in Kotlin, reading messages from SQS, publishing results on a SNS topic, sending a mail with a subscription.

Made with :heart: using Serverless Framework.

It will create 3 resources:
* A SQS Queue
* A SNS Topic
* A Mail subscription for the created Topic
