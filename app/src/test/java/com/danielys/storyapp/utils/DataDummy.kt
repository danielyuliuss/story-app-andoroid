package com.danielys.storyapp.utils

import com.danielys.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummy(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..100) {
            val news = ListStoryItem(
                "www.test.com",
                "2022-02-22",
                "Test",
                "Test Description",
                "89",
                "$i",
                "27"
            )
            storyList.add(news)
        }
        return storyList
    }
}