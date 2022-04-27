package com.centraldocs.centraldocs.data

import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.model.Topic

class Datasource {
    fun loadTopics(): List<Topic>{

        return listOf(
            Topic(R.string.c, SubTopicData().assignTopic("C#").loadSubTopics()) ,
            Topic(R.string.java, SubTopicData().assignTopic("Java").loadSubTopics()),
            Topic(R.string.kotlin, SubTopicData().assignTopic("Kotlin").loadSubTopics())
        )

    }

}