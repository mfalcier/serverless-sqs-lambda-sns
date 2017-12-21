package com.example

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.slf4j.LoggerFactory
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages

class Handler:RequestHandler<Map<String, Any>, Any> {
    override fun handleRequest(input:Map<String, Any>, context:Context):Any {

        val sqs = AmazonSQSClientBuilder.defaultClient()
        val url = sqs.getQueueUrl("serverless-sqs-lambda-sns-dev-queue").queueUrl

        val receiveMessageRequest = ReceiveMessageRequest(url)
        val messages = sqs.receiveMessage(receiveMessageRequest).messages
        for (message in messages) {
            LOG.info("Message: {}", message.body)

            val sns = AmazonSNSClientBuilder.defaultClient()
            val topic = sns.createTopic("serverless-sqs-lambda-sns-dev-topic")
            val publishRequest = PublishRequest(topic.topicArn, message.body)
            val publishResult = sns.publish(publishRequest)
            LOG.info("Topic Result: {}", publishResult)
        }
        return Any()
    }
    companion object {
        private val LOG = LoggerFactory.getLogger(Handler::class.java)
    }
}
