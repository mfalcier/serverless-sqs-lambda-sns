package com.example

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import org.slf4j.LoggerFactory

class Handler:RequestHandler<Map<String, Any>, Any> {
    override fun handleRequest(input:Map<String, Any>, context:Context):Any {
        // Initializing SQS client
        val sqs = AmazonSQSClientBuilder.defaultClient()
        val sqsUrl = System.getenv("sqsUrl")

        // Creating SQS Request with timeout
        val receiveMessageRequest = ReceiveMessageRequest(sqsUrl)
        receiveMessageRequest.visibilityTimeout = 600

        // Retrieving messages and processing them
        val messages = sqs.receiveMessage(receiveMessageRequest).messages
        for (message in messages) {
            LOG.info("Message: {}", message.body)

            // Initializing SNS client
            val sns = AmazonSNSClientBuilder.defaultClient()
            val snsArn = System.getenv("snsArn")

            // Creating topic request and publishing it
            val publishRequest = PublishRequest(snsArn, message.body)
            val publishResult = sns.publish(publishRequest)

            // Deleting SQS Message
            val messageReceiptHandle = message.receiptHandle
            sqs.deleteMessage(DeleteMessageRequest(sqsUrl, messageReceiptHandle))

            LOG.info("Topic Result: {}", publishResult)
        }
        return Any()
    }
    companion object {
        private val LOG = LoggerFactory.getLogger(Handler::class.java)
    }
}