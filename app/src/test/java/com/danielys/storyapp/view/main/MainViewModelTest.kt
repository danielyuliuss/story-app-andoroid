package com.danielys.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.danielys.storyapp.data.UserPreferences
import com.danielys.storyapp.data.response.ListStoryItem
import com.danielys.storyapp.utils.DataDummy
import com.danielys.storyapp.utils.ListUpdateCallback
import com.danielys.storyapp.utils.MainDispatcherRule
import com.danielys.storyapp.utils.MockPagingSource
import com.danielys.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var preferences: UserPreferences

    private lateinit var mainViewModel: MainViewModel
    private val dummyData = DataDummy.generateDummy()

    @Before
    fun setup() {
        mainViewModel = MainViewModel(preferences)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val data: PagingData<ListStoryItem> = MockPagingSource.snapshot(dummyData)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        mainViewModel.storiesData = expectedStory
        val actualStory: PagingData<ListStoryItem> = mainViewModel.storiesData.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = ListUpdateCallback.listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyData[0], differ.snapshot()[0])
        assertEquals(dummyData.size, differ.snapshot().size)
    }

    @Test
    fun `when Get Stories Empty Should Return Empty Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        mainViewModel.storiesData = expectedStory

        val actualStory: PagingData<ListStoryItem> = mainViewModel.storiesData.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = ListUpdateCallback.listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }


}


