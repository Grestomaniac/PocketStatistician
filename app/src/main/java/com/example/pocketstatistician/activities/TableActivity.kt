package com.example.pocketstatistician.activities

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketstatistician.*
import com.example.pocketstatistician.adapters.table.DataPlaceHolderItemAdapter
import com.example.pocketstatistician.adapters.table.DataPlaceholderAdapter
import com.example.pocketstatistician.adapters.table.NotePlaceholderAdapter
import com.example.pocketstatistician.adapters.table.VariablePlaceholderAdapter
import com.example.pocketstatistician.convenience.CustomRecyclerView
import com.example.pocketstatistician.convenience.VariantChooserDialog
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class TableActivity: AppCompatActivity() {

    lateinit var variablePlaceholder: CustomRecyclerView
    lateinit var notePlaceholder: CustomRecyclerView
    lateinit var dataPlaceholder: RecyclerView
    lateinit var pickerEditText: EditText
    lateinit var pickerTextView: TextView
    lateinit var gestureView: ViewGroup
    lateinit var mDetector: GestureDetector
    lateinit var pickedNote: TextView
    lateinit var pickedVariable: TextView
    lateinit var navigatorButton: Button
    lateinit var navigatorTextField: NavigatorTextField

    lateinit var verticalScrollListener: RecyclerView.OnScrollListener
    lateinit var horizontalScrollListener: RecyclerView.OnScrollListener
    val selectedView: SelectedView = SelectedView()
    var scrollingOnOtherAxis = false

    lateinit var statistic: Statistic
    lateinit var variableNames: RealmList<String>
    lateinit var variableTypes: RealmList<Type>
    lateinit var data: RealmList<Note>
    lateinit var arrayOfSizes: ArrayList<Int>
    var layoutHeight = 50
    var coefficient: Int = 20
    var recViewPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.table_layout)

        val statPosition = intent.getIntExtra("statistic_number", -1)
        statistic = (application as Application).statistics[statPosition]!!
        variableNames = statistic.variable_names
        variableTypes = statistic.variable_types
        data = statistic.data
        title = statistic.name

        arrayOfSizes = getMaxSizes()
        verticalScrollListener = object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView == notePlaceholder) scrollAttachedView(dataPlaceholder, dy)
                else scrollAttachedView(notePlaceholder, dy)
            }
            private fun scrollAttachedView(recyclerView: RecyclerView, dy: Int) {
                recyclerView.removeOnScrollListener(verticalScrollListener)
                recyclerView.scrollBy(0, dy)
                recyclerView.addOnScrollListener(verticalScrollListener)
            }
        }

        horizontalScrollListener = object: RecyclerView.OnScrollListener() {
            var scrollMomentum = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                disconnectListenersExceptOne(recyclerView)
                scrollAllRecyclerViewsExceptOne(recyclerView, dx)
                connectChildrenListenersExceptOne(recyclerView)
                scrollMomentum += dx
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    val linearManager = variablePlaceholder.layoutManager as LinearLayoutManager
                    val position = if (scrollMomentum < 0) linearManager.findFirstVisibleItemPosition() else linearManager.findLastVisibleItemPosition()
                    scrollAllHorizontallyToPosition(position)
                    recViewPosition = position
                    scrollMomentum = 0
                }
            }
        }

        mDetector = GestureDetector(this, NavigationGestureDetector(this))

        val touchListener = View.OnTouchListener { v, event ->
            mDetector.onTouchEvent(event)
        }

        variablePlaceholder = findViewById(R.id.variable_placeholder)
        notePlaceholder = findViewById(R.id.note_placeholder)
        dataPlaceholder = findViewById(R.id.data_placeholder)
        pickerEditText = findViewById(R.id.picker_edit_text)
        pickerTextView = findViewById(R.id.picker_text_view)
        pickedNote = findViewById(R.id.note)
        pickedVariable = findViewById(R.id.variable)
        navigatorButton = findViewById(R.id.navigator_button)
        navigatorTextField = NavigatorTextField(pickerTextView, pickerEditText)

        gestureView = findViewById(R.id.gesture_detector)

        pickerTextView.setOnClickListener { showVariantPickerDialog() }

        variablePlaceholder.adapter = VariablePlaceholderAdapter(variableNames, arrayOfSizes)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        variablePlaceholder.layoutManager = layoutManager

        notePlaceholder.adapter = NotePlaceholderAdapter(data, convertToPx(layoutHeight))
        notePlaceholder.layoutManager = LinearLayoutManager(this)
        notePlaceholder.addOnScrollListener(verticalScrollListener)

        dataPlaceholder.adapter = DataPlaceholderAdapter(data, this, arrayOfSizes, horizontalScrollListener)
        dataPlaceholder.layoutManager = LinearLayoutManager(this)
        dataPlaceholder.addOnScrollListener(verticalScrollListener)

        gestureView.setOnTouchListener(touchListener)

        val stoppedListener = object: CustomRecyclerView.OnScrollStoppedListener {
            override fun onScrollStopped() {
                selectViewOnScrollStopped()
            }
        }
        notePlaceholder.setOnScrollStoppedListener(stoppedListener)
        variablePlaceholder.setOnScrollStoppedListener(stoppedListener)
        notePlaceholder.isHorizontal = false

    }

    fun selectViewOnScrollStopped() {
        if (scrollingOnOtherAxis) {
            scrollingOnOtherAxis = false
            notePlaceholder.stopScrollTask()
        }
        val view = getViewFromData(selectedView.notePosition, selectedView.variablePosition)
        selectedView.view = view
        selectedView.view!!.background = getDrawable(R.drawable.table_selected)
        attachToNavigator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        dataPlaceholder.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
            }

            override fun onChildViewAttachedToWindow(view: View) {
                val holder = dataPlaceholder.getChildViewHolder(view) as DataPlaceholderAdapter.ViewHolder
                holder.row.clearOnScrollListeners()
                holder.row.scrollToPosition(recViewPosition)
                holder.row.addOnScrollListener(horizontalScrollListener)
            }
        })
    }

    private fun getViewFromData(notePosition: Int, variablePosition: Int): TextView {
        val noteView = dataPlaceholder.findViewHolderForAdapterPosition(notePosition) as DataPlaceholderAdapter.ViewHolder
        return (noteView.row.findViewHolderForAdapterPosition(variablePosition) as DataPlaceHolderItemAdapter.ViewHolder).textView
    }

    private fun getMaxSizes(): ArrayList<Int> {
        val sizes = variableNames.mapTo(ArrayList(), { it.length })
        val columnCount = sizes.size

        for (i in 0 until data.size) {
            for (j in 0 until columnCount) {
                val l = data[i]!!.note[j]!!.length
                if (l > sizes[j]) sizes[j] = l
            }
        }

        sizes.replaceAll { convertToPx(it * coefficient) }
        sizes.add(convertToPx(layoutHeight))

        var str = ""
        for (s in sizes) str += "$s "
        log(str)

        return sizes
    }

    fun convertToPx(dps: Int): Int {
        val scale: Float = this.resources.displayMetrics.density
        return (dps * scale + 0.5f).toInt()
    }

    fun disconnectListenersExceptOne(except: RecyclerView) {
        if (except == variablePlaceholder) {
            disconnectAllListeners()
            return
        }

        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.removeOnScrollListener(horizontalScrollListener)
        }
        variablePlaceholder.removeOnScrollListener(horizontalScrollListener)
    }

    fun disconnectAllListeners() {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.removeOnScrollListener(horizontalScrollListener)
        }
    }

    fun scrollAllRecyclerViewsExceptOne(except: RecyclerView, dx: Int) {
        if (except == variablePlaceholder) {
            scrollAllRecyclerViewsBy(dx)
            return
        }
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.scrollBy(dx, 0)
        }
        variablePlaceholder.scrollBy(dx, 0)
    }

    fun scrollAllRecyclerViewsBy(dx: Int) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.scrollBy(dx, 0)
        }
    }

    fun connectChildrenListenersExceptOne(except: RecyclerView) {
        if (except == variablePlaceholder) {
            connectAllChildrenListeners()
            return
        }
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.addOnScrollListener(horizontalScrollListener)
        }
        variablePlaceholder.addOnScrollListener(horizontalScrollListener)
    }

    fun connectAllChildrenListeners() {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.addOnScrollListener(horizontalScrollListener)
        }
    }

    fun moveAllRecyclerViewsToPosition(position: Int) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.scrollToPosition(position)
        }
        variablePlaceholder.scrollToPosition(position)
        recViewPosition = position
    }

    fun scrollAllHorizontallyToPosition(position: Int) {
        disconnectAllListeners()
        moveAllRecyclerViewsToPosition(position)
        connectAllChildrenListeners()
    }

    fun scrollAllVerticallyToPosition(verticalPosition: Int) {
        dataPlaceholder.removeOnScrollListener(verticalScrollListener)
        dataPlaceholder.post { dataPlaceholder.scrollToPosition(verticalPosition) }
        notePlaceholder.post { notePlaceholder.scrollToPosition(verticalPosition) }
        dataPlaceholder.addOnScrollListener(verticalScrollListener)
    }

    fun processDataOnClick(view: View, notePosition: Int, variablePosition: Int) {
        if (view != selectedView.view) {
            changeSelectedView(view, notePosition, variablePosition)
            attachToNavigator()
        }
        else {
            selectedView.view?.background = getDrawable(R.drawable.table_data)
            deAttachFromNavigator()
        }
    }

    fun selectViewAtPosition(notePosition: Int, variablePosition: Int) {
        val selected = getViewAt(notePosition, variablePosition)
        changeSelectedView(selected, notePosition, variablePosition)
        if (selected == null) return
        attachToNavigator()
    }

    fun changeSelectedView(view: View?, notePosition: Int, variablePosition: Int) {
        selectedView.view?.background = getDrawable(R.drawable.table_data)
        selectedView.notePosition = notePosition
        selectedView.variablePosition = variablePosition
        if (view == null) return
        selectedView.view = view as TextView
        selectedView.view!!.background = getDrawable(R.drawable.table_selected)
    }

    fun getAdapterPositionRange(holder: RecyclerView): Array<Int> {
        val layoutManager = holder.layoutManager as LinearLayoutManager
        val firstNotePos = layoutManager.findFirstVisibleItemPosition()
        val secondNotePos = layoutManager.findLastVisibleItemPosition()
        log("first visible $firstNotePos, second visible $secondNotePos")
        return arrayOf(firstNotePos, secondNotePos)
    }

    fun getAdapterPositionRangeForBoth(): VisibleRange {
        val verticalRange = getAdapterPositionRange(variablePlaceholder)
        val horizontalRange = getAdapterPositionRange(notePlaceholder)
        return VisibleRange(horizontalRange[0], horizontalRange[1], verticalRange[0], verticalRange[1])
    }

    fun getViewAt(notePosition: Int, variablePosition: Int): View? {
        val visibleRange = getAdapterPositionRangeForBoth()
        log("n1=${visibleRange.n1pos} n2=${visibleRange.n2pos} v1=${visibleRange.v1pos} v2=${visibleRange.v2pos}")
        val checkIfContains = visibleRange.checkIfContains(notePosition, variablePosition)
        when (checkIfContains) {
            3 -> {
                val noteHolder = dataPlaceholder.findViewHolderForAdapterPosition(notePosition) as DataPlaceholderAdapter.ViewHolder
                val variableHolder = noteHolder.row.findViewHolderForAdapterPosition(variablePosition) as DataPlaceHolderItemAdapter.ViewHolder

                if (dataPlaceholder.layoutManager!!.isViewPartiallyVisible(noteHolder.itemView, false, true)) scrollAllVerticallyToPosition(notePosition)
                if (noteHolder.row.layoutManager!!.isViewPartiallyVisible(variableHolder.itemView, false, true)) scrollAllHorizontallyToPosition(variablePosition)

                return variableHolder.textView
            }
            2 -> {
                log("not visible vertically")
                scrollAllVerticallyToPosition(notePosition)
                notePlaceholder.startScrollerTask()
                return null
            }
            1 -> {
                log("not visible horizontally")
                scrollAllHorizontallyToPosition(variablePosition)
                variablePlaceholder.startScrollerTask()
                return null
            }
            else -> {
                log("not visible")
                scrollAllHorizontallyToPosition(variablePosition)
                scrollAllVerticallyToPosition(notePosition)
                scrollingOnOtherAxis = true
                variablePlaceholder.startScrollerTask()
                notePlaceholder.startScrollerTask()
                return null
            }
        }
    }

    fun attachToNavigator() {
        if (variableTypes[selectedView.variablePosition]!!.type == "classified") {
            navigatorTextField.setTextViewVisible(selectedView.view!!.text)
        }
        else {
            navigatorTextField.setEditTextVisible(selectedView.view!!.text)
            pickerEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        pickedVariable.text = variableNames[selectedView.variablePosition]
        pickedNote.text = selectedView.notePosition.plus(1).toString()
    }

    fun deAttachFromNavigator() {
        selectedView.view = null
        navigatorTextField.setTextViewVisible("")
        pickedVariable.text = ""
        pickedNote.text = ""
    }

    fun showVariantPickerDialog() {
        val dialog = VariantChooserDialog(variableTypes[selectedView.variablePosition]!!.variants, this, pickerTextView)
        dialog.show()
    }

    fun onNavigatorButtonClick(v: View) {
        if (selectedView.view == null) return

        selectedView.view!!.text = navigatorTextField.getText()
        selectNext()
    }

    fun selectNext() {
        if (selectedView.variablePosition < variableNames.size - 1) selectViewAtPosition(selectedView.notePosition, selectedView.variablePosition + 1)
        else if (selectedView.notePosition < data.size - 1) selectViewAtPosition(selectedView.notePosition + 1, 0)
        else {
            //TODO(show note dialog)
        }
    }

    class SelectedView(var view: TextView? = null, var variablePosition: Int = 0, var notePosition: Int = 0)

    class NavigatorTextField(private val textView: TextView, private val editText: EditText) {
        fun getText(): CharSequence {
            return if (textView.isVisible) textView.text
            else editText.text
        }

        fun setTextViewVisible(text: CharSequence) {
            textView.visibility = View.VISIBLE
            editText.visibility = View.GONE
            textView.text = text
        }

        fun setEditTextVisible(text: CharSequence) {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            editText.setText(text)
        }

        fun setText(text: CharSequence) {
            if (textView.isVisible) textView.text = text
            else editText.setText(text)
        }
    }

    class VisibleRange(val n1pos: Int, val n2pos: Int, val v1pos: Int, val v2pos: Int) {
        fun checkIfContains(notePosition: Int, variablePosition: Int): Int {
            val containsVertically = isContainsVertically(notePosition)
            val containsHorizontally = isContainsHorizontally(variablePosition)
            if (containsHorizontally && containsVertically) return 3 //visible
            if (containsHorizontally) return 2 //not visible vertically
            if (containsVertically) return 1 //not visible horizontally
            return 0 //not visible
        }

        private fun isContainsHorizontally(variablePosition: Int): Boolean {
            return !(v1pos > variablePosition || v2pos < variablePosition)
        }

        private fun isContainsVertically(notePosition: Int): Boolean {
            return !(n1pos > notePosition || n2pos < notePosition)
        }
    }

}