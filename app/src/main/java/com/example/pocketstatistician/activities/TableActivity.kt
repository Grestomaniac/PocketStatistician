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
import com.example.pocketstatistician.convenience.VariantChooserDialog
import com.example.pocketstatistician.convenience.log
import io.realm.RealmList

class TableActivity: AppCompatActivity() {

    lateinit var variablePlaceholder: RecyclerView
    lateinit var notePlaceholder: RecyclerView
    lateinit var dataPlaceholder: RecyclerView
    lateinit var pickerEditText: EditText
    lateinit var pickerTextView: TextView
    lateinit var gestureView: ViewGroup
    lateinit var mDetector: GestureDetector
    lateinit var pickedNote: TextView
    lateinit var pickedVariable: TextView
    lateinit var navigatorButton: Button
    lateinit var navigatorTextField: NavigatorTextField

    val verticalScrollListeners: ArrayList<RecyclerView.OnScrollListener> = ArrayList()
    private val horizontalScrollListeners: ArrayList<RecyclerView.OnScrollListener> = ArrayList()
    lateinit var scrollStateChangedListener: RecyclerView.OnScrollListener
    val selectedView: SelectedView = SelectedView()

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
        verticalScrollListeners.add(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                dataPlaceholder.removeOnScrollListener(verticalScrollListeners[1])
                dataPlaceholder.scrollBy(0, dy)
                dataPlaceholder.addOnScrollListener(verticalScrollListeners[1])
            }
        })
        verticalScrollListeners.add(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                notePlaceholder.removeOnScrollListener(verticalScrollListeners[0])
                notePlaceholder.scrollBy(0, dy)
                notePlaceholder.addOnScrollListener(verticalScrollListeners[0])
            }
        })

        verticalScrollListeners.add(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    super.onScrollStateChanged(recyclerView, newState)
                    log("catched")
                    val view = getViewFromData(selectedView.notePosition, selectedView.variablePosition)
                    selectedView.view = view
                    selectedView.view!!.background = getDrawable(R.drawable.table_selected)
                    attachToNavigator()
                    notePlaceholder.removeOnScrollListener(this)
                }
            }
        })

        horizontalScrollListeners.add(object: RecyclerView.OnScrollListener() {
            var scrollMomentum = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                disconnectAllListeners()
                moveAllRecyclerViews(dx)
                connectAllChildrenListeners()
                scrollMomentum += dx
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val linearManager = variablePlaceholder.layoutManager as LinearLayoutManager
                    val position = if (scrollMomentum < 0) linearManager.findFirstVisibleItemPosition() else linearManager.findLastVisibleItemPosition()
                    scrollAllHorizontallyToPosition(position)
                    recViewPosition = position
                    scrollMomentum = 0
                }
            }
        })

        horizontalScrollListeners.add(object: RecyclerView.OnScrollListener() {
            var scrollMomentum = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                disconnectListenersExceptOne(recyclerView)
                moveRecyclerViewsExceptOne(recyclerView, dx)
                connectChildrenListenersExceptOne(recyclerView)
                scrollMomentum += dx
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val linearManager = variablePlaceholder.layoutManager as LinearLayoutManager
                    val position = if (scrollMomentum < 0) linearManager.findFirstVisibleItemPosition() else linearManager.findLastVisibleItemPosition()
                    scrollAllHorizontallyToPosition(position)
                    recViewPosition = position
                    scrollMomentum = 0
                }
            }
        })

        scrollStateChangedListener = object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                log("scrolling")
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                log("state have changed")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    log("catched")
                    val view = getViewFromData(selectedView.notePosition, selectedView.variablePosition)
                    selectedView.view = view
                    selectedView.view!!.background = getDrawable(R.drawable.table_selected)
                    attachToNavigator()
                    variablePlaceholder.removeOnScrollListener(this)
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
        variablePlaceholder.addOnScrollListener(horizontalScrollListeners[0])

        notePlaceholder.adapter = NotePlaceholderAdapter(data, convertToPx(layoutHeight))
        notePlaceholder.layoutManager = LinearLayoutManager(this)
        notePlaceholder.addOnScrollListener(verticalScrollListeners[0])

        dataPlaceholder.adapter = DataPlaceholderAdapter(data, this, arrayOfSizes, horizontalScrollListeners[1])
        dataPlaceholder.layoutManager = LinearLayoutManager(this)
        dataPlaceholder.addOnScrollListener(verticalScrollListeners[1])

        gestureView.setOnTouchListener(touchListener)

        dataPlaceholder.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
            }

            override fun onChildViewAttachedToWindow(view: View) {
                val holder = dataPlaceholder.getChildViewHolder(view) as DataPlaceholderAdapter.ViewHolder
                holder.row.scrollToPosition(recViewPosition)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.removeOnScrollListener(horizontalScrollListeners[1])
        }
        variablePlaceholder.removeOnScrollListener(horizontalScrollListeners[0])
    }

    fun disconnectAllListeners() {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.removeOnScrollListener(horizontalScrollListeners[1])
        }
    }

    fun moveRecyclerViewsExceptOne(except: RecyclerView? = null, dx: Int) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.scrollBy(dx, 0)
        }
        variablePlaceholder.scrollBy(dx, 0)
    }

    fun moveAllRecyclerViews(dx: Int) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.scrollBy(dx, 0)
        }
    }

    fun connectChildrenListenersExceptOne(except: RecyclerView? = null) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            if (rv.row != except) rv.row.addOnScrollListener(horizontalScrollListeners[1])
        }
        variablePlaceholder.addOnScrollListener(horizontalScrollListeners[0])
    }

    fun connectAllChildrenListeners() {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.addOnScrollListener(horizontalScrollListeners[1])
        }
    }

    fun moveAllRecyclerViewsToPosition(position: Int) {
        for (i in 0 until dataPlaceholder.childCount) {
            val rv = dataPlaceholder.getChildViewHolder(dataPlaceholder.getChildAt(i)) as DataPlaceholderAdapter.ViewHolder
            rv.row.scrollToPosition(position)
        }
        variablePlaceholder.scrollToPosition(position)
    }

    fun scrollAllHorizontallyToPosition(position: Int) {
        disconnectAllListeners()
        variablePlaceholder.removeOnScrollListener(horizontalScrollListeners[0])
        moveAllRecyclerViewsToPosition(position)
        connectAllChildrenListeners()
        variablePlaceholder.addOnScrollListener(horizontalScrollListeners[0])
    }

    fun scrollAllVerticallyToPosition(verticalPosition: Int) {
        dataPlaceholder.removeOnScrollListener(verticalScrollListeners[1])
        notePlaceholder.removeOnScrollListener(verticalScrollListeners[0])
        dataPlaceholder.scrollToPosition(verticalPosition)
        notePlaceholder.scrollToPosition(verticalPosition)
        dataPlaceholder.addOnScrollListener(verticalScrollListeners[1])
        notePlaceholder.addOnScrollListener(verticalScrollListeners[0])
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

    fun getViewAt(notePosition: Int, variablePosition: Int): View? {
        val noteView = dataPlaceholder.findViewHolderForAdapterPosition(notePosition)
        if (noteView == null) {
            log("view at n=${notePosition+1} v=${variableNames[variablePosition]} is not visible vertically")
            scrollAllVerticallyToPosition(notePosition)
            dataPlaceholder.removeOnScrollListener(verticalScrollListeners[2])
            dataPlaceholder.addOnScrollListener(verticalScrollListeners[2])
            return null
        }
        else {
            if (dataPlaceholder.layoutManager!!.isViewPartiallyVisible(noteView.itemView, false, true)) {
                log("view at n=${notePosition+1} v=${variableNames[variablePosition]} is not completely visible, moving now vertically")
                scrollAllVerticallyToPosition(notePosition)
            }
            else log("view at n=${notePosition+1} v=${variableNames[variablePosition]} is completely visible vertically")
            return getViewAtVariablePosition(variablePosition, noteView as DataPlaceholderAdapter.ViewHolder)
        }
    }

    fun getViewAtVariablePosition(variablePosition: Int, noteHolder: DataPlaceholderAdapter.ViewHolder): View? {
        val variableView = noteHolder.row.findViewHolderForAdapterPosition(variablePosition)
        if (variableView != null) {
            if (noteHolder.row.layoutManager!!.isViewPartiallyVisible(variableView.itemView, false, true)) {
                scrollAllHorizontallyToPosition(variablePosition)
                log("view is not completely visible, moving now horizontally")
            }
            else log("view is completely visible")
            return (variableView as DataPlaceHolderItemAdapter.ViewHolder).textView
        } else {
            scrollAllHorizontallyToPosition(variablePosition)
            variablePlaceholder.removeOnScrollListener(scrollStateChangedListener)
            variablePlaceholder.addOnScrollListener(scrollStateChangedListener)
            log("view is not visible, moving now horizontally")
            return null
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

}