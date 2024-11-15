import React from "react";
import useAttendanceStore from "@/store/useAttendanceStore";
import styles from "@/styles/Home/Todo.module.css";

const TodoList = () => {
  const userTodoData = useAttendanceStore((state) => state.userTodoData);
  const todos = Array.isArray(userTodoData) ? userTodoData : [];
  return (
    <div>
      <ul className={styles.todoList}>
        {todos.length > 0 ? (
          todos.map((item, index) => (
            <li key={index} className={styles.todoItem}>
              <label>
                <input type="checkbox" className={styles.checkIcon} />
                <span className={styles.labelText}>{item.name}</span>
              </label>
            </li>
          ))
        ) : (
          <li className={styles.todoItem}>일정이 없습니다.</li>
        )}
      </ul>
    </div>
  );
};

export default TodoList;
