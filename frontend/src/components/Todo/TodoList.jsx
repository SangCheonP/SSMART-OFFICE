import React from "react";
import styles from "../../styles/Home/Todo.module.css";

const TodoList = () => {
  return (
    <div>
      <ul className={styles.todoList}>
        <li className={styles.todoItem}>
          <label>
            <input type="checkbox" className={styles.checkIcon} />
            <span className={styles.labelText}>코드 리뷰 반영 및 수정</span>
          </label>
        </li>
      </ul>
    </div>
  );
};

export default TodoList;
