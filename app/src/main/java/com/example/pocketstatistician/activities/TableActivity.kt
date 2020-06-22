package com.example.pocketstatistician.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
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
import com.example.pocketstatistician.convenience.*
import io.realm.Realm
import io.realm.RealmList

class TableActivity: AppCompatActivity() {

    lateinit var variablePlaceholder: RecyclerViewWithStopListener
    lateinit var notePlaceholder: RecyclerViewWithStopListener
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
    private val changes: ArrayList<DataChange> = ArrayList()
    private val actions: ArrayList<Action> = ArrayList()

    lateinit var statistic: Statistic
    lateinit var variables: RealmList<Variable>
    val data: RealmList<Note> = RealmList()
    lateinit var arrayOfSizes: ArrayList<Int>
    var layoutHeight = 50
    var coefficient: Int = 18
    var recViewPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.table_layout)

        val statPosition = intent.getIntExtra("statistic_position", -1)
        statistic = (application as Application).statistics[statPosition]!!
        variables = statistic.variables
        data.addAll(statistic.data)
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

        variablePlaceholder = findViewById(R.id.variant_placeholder)
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

        variablePlaceholder.adapter = VariablePlaceholderAdapter(variables, arrayOfSizes)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        variablePlaceholder.layoutManager = layoutManager

        val noteAdapter = object : NotePlaceholderAdapter(data, convertToPx(layoutHeight, this)) {
            override fun onAddNoteButtonClick() {
                addNote()
            }
        }
        notePlaceholder.adapter = noteAdapter
        notePlaceholder.layoutManager = LinearLayoutManager(this)
        notePlaceholder.addOnScrollListener(verticalScrollListener)

        dataPlaceholder.adapter = DataPlaceholderAdapter(data, this, arrayOfSizes)
        dataPlaceholder.layoutManager = LinearLayoutManager(this)
        dataPlaceholder.addOnScrollListener(verticalScrollListener)

        gestureView.setOnTouchListener(touchListener)

        val stoppedListener = object: RecyclerViewWithStopListener.OnScrollStoppedListener {
            override fun onScrollStopped() {
                selectViewOnScrollStopped()
            }
        }
        notePlaceholder.setOnScrollStoppedListener(stoppedListener)
        variablePlaceholder.setOnScrollStoppedListener(stoppedListener)
        notePlaceholder.isHorizontal = false

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
        menuInflater.inflate(R.menu.table_menu, menu)
        return true
    }

    private fun getViewFromData(notePosition: Int, variablePosition: Int): TextView {
        val noteView = dataPlaceholder.findViewHolderForAdapterPosition(notePosition) as DataPlaceholderAdapter.ViewHolder
        return (noteView.row.findViewHolderForAdapterPosition(variablePosition) as DataPlaceHolderItemAdapter.ViewHolder).textView
    }

    private fun getMaxSizes(): ArrayList<Int> {
        val sizes = variables.mapTo(ArrayList(), { it.name.length })

        for (i in 0 until variables.size) {
            val variableType = variables[i]!!.type!!
            if (variableType.type == "classified") {
                val variants = variableType.variants
                for (j in 0 until variants.size) {
                    if (sizes[i] < variants[j]!!.length) sizes[i] = variants[j]!!.length
                }
            }
        }

        sizes.replaceAll { convertToPx(it * coefficient, this) }
        sizes.add(convertToPx(layoutHeight, this))

        var str = ""
        for (s in sizes) str += "$s "
        log(str)

        return sizes
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
        if (variables[selectedView.variablePosition]!!.type!!.type == "classified") {
            navigatorTextField.setTextViewVisible(selectedView.view!!.text)
        }
        else {
            navigatorTextField.setEditTextVisible(selectedView.view!!.text)
            pickerEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        pickedVariable.text = variables[selectedView.variablePosition]!!.name
        pickedNote.text = selectedView.notePosition.plus(1).toString()
    }

    fun deAttachFromNavigator() {
        selectedView.view = null
        navigatorTextField.setTextViewVisible("")
        pickedVariable.text = ""
        pickedNote.text = ""
    }

    fun showVariantPickerDialog() {
        val selectedVariable = variables[selectedView.variablePosition]!!
        val variants = selectedVariable.type!!.variants

        val dialog = VariantChooserDialog(variants, selectedVariable.name,this)
        dialog.onVariantChosenListener = object : VariantChooserDialog.OnVariantChosenListener {
            override fun onVariantChosen(itemPos: Int) {
                navigatorTextField.setText(variants[itemPos]!!)
                onNavigatorButtonClick(null)
            }
        }
        dialog.show()
    }

    fun onNavigatorButtonClick(v: View?) {
        if (selectedView.view == null) return

        if (selectedView.view!!.text != navigatorTextField.getText()) {
            selectedView.view!!.text = navigatorTextField.getText()
            log("its changing")
            addChange()
        }

        selectNext()
    }

    fun selectNext() {
        if (selectedView.variablePosition < variables.size - 1) {
            selectViewAtPosition(selectedView.notePosition, selectedView.variablePosition + 1)
        }
        else if (selectedView.notePosition < data.size - 1) {
            selectViewAtPosition(selectedView.notePosition + 1, 0)
        }
        else {
            addNote()
        }
    }

    fun addChange() {
        for (change in changes) {
            if (!(change.varPosition == selectedView.variablePosition && change.notePosition == selectedView.notePosition)) continue
            else {
                change.buffer = selectedView.view!!.text.toString()
                return
            }
        }
        changes.add(DataChange(selectedView.view!!.text.toString(), selectedView.notePosition, selectedView.variablePosition))
    }

    fun addNote() {
        val dialog = YouChooseDialog(getString(R.string.do_you_want_to_add_note), getString(R.string.yes), getString(R.string.no))
        dialog.dialogEventHandler = object : YouChooseDialog.DialogClickListener {
            override fun onNegativeButtonClick() {
            }

            override fun onPositiveButtonClick() {
                onAddNoteDialogPositive()
            }
        }
        dialog.show(supportFragmentManager, "addNote")
    }

    fun onAddNoteDialogPositive() {
        val position = dataPlaceholder.adapter!!.itemCount - 1
        log("adding note at position = $position")
        actions.add(Action("addNote", position))
        data.add(getEmptyNote(variables.size))
        notePlaceholder.adapter!!.notifyItemInserted(position)
        dataPlaceholder.adapter!!.notifyItemInserted(position)
    }

    fun onSave() {
        Realm.getDefaultInstance().executeTransaction { realm ->
            for (action in actions) {
                if (action.actionType == "addNote") {
                    statistic.data.add(getEmptyNote(variables.size))
                }
                else if (action.actionType == "deleteNote") {
                    statistic.data.removeAt(action.notePosition)
                    for (change in changes) {
                        if (change.notePosition == action.notePosition) changes.remove(change)
                    }
                }
            }
            for (change in changes) {
                statistic.data[change.notePosition]!!.note[change.varPosition] = change.buffer
            }
            actions.clear()
            changes.clear()
        }
        show(this, getString(R.string.saved))
    }

    fun onCancel() {

    }

    class SelectedView(var view: TextView? = null, var variablePosition: Int = 0, var notePosition: Int = 0)

    inner class NavigatorTextField(private val textView: TextView, private val editText: EditText) {
        init {
            editText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onNavigatorButtonClick(null)
                }
                false
            }
        }

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

    class DataChange(var buffer: String, val notePosition: Int, val varPosition: Int)
    class Action(val actionType: String, val notePosition: Int)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            true
        }

        R.id.action_save -> {
            currentFocus?.clearFocus()
            onSave()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}