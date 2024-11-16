import React, { useState } from "react";
import styles from "@/styles/Home/Todo.module.css";
import { checkEvent } from "@/services/homeApi";

const TodoList = ({ todos = [] }) => {
  const [localTodos, setLocalTodos] = useState(todos);
  const handleCheckboxChange = async (id) => {
    try {
      setLocalTodos((prevTodos) =>
        prevTodos.map((todo) =>
          todo.id === id ? { ...todo, completed: !todo.completed } : todo
        )
      );
      await checkEvent(id);
      console.log(`아이디${id} 토글완료됨`);
    } catch (error) {
      console.error("토글완료 에러:", error);
      setLocalTodos((prevTodos) =>
        prevTodos.map((todo) =>
          todo.id === id ? { ...todo, completed: !todo.completed } : todo
        )
      );
    }
  };

  return (
    <div>
      <ul className={styles.todoList}>
        {todos.length > 0 ? (
          todos.map((item, index) => (
            <li key={index} className={styles.todoItem}>
              <label>
                <input
                  type="checkbox"
                  className={styles.checkIcon}
                  checked={item.completed}
                  onChange={() => handleCheckboxChange(item.id)}
                />
                <span className={styles.labelText}>{item.name}</span>
              </label>
              <span className={styles.description_text}>
                {item.description}
              </span>
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
