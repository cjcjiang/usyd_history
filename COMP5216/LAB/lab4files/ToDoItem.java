package au.edu.sydney.comp5216;

import com.google.common.base.Strings;
import com.orm.SugarRecord;

public class ToDoItem extends SugarRecord {
    public String todo;

    public ToDoItem(){}

    public ToDoItem(String ToDo){
        this.todo = ToDo;
    }
}
