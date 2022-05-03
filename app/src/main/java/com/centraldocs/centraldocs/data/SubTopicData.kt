package com.centraldocs.centraldocs.data

import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.model.Subtopic

class SubTopicData() {
    private var topic: String = ""

    fun assignTopic(_topic: String): SubTopicData {
        topic = _topic
        return this
    }

    private var subtopic_list : List<Subtopic> = mutableListOf()

    fun loadSubTopics(): List<Subtopic>{
        when(topic){
            "C#" -> subtopic_list=listOf(
                Subtopic(R.string.classes),
                Subtopic(R.string.c_random),
                Subtopic(R.string.c_next_md),
                Subtopic(R.string.c_objects),
                Subtopic(R.string.c_interfaces),
                Subtopic(R.string.c_generic),
                Subtopic(R.string.c_igeneric),
                Subtopic(R.string.c_iobject)
            )
            "Java" -> subtopic_list=listOf(
                Subtopic(R.string.classes),
                Subtopic(R.string.objects)
            )
            "Kotlin" -> subtopic_list=listOf(
                Subtopic(R.string.classes),
                Subtopic(R.string.k_any)
            )
        }

        return subtopic_list
    }
}