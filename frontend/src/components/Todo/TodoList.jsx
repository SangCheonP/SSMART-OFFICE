import React from "react";
import styles from "@/styles/Home/Todo.module.css";
import useHomeStore from "@/store/useHomeStore";

const TodoList = ({ todos }) => {
  const { setTodoData, checkEvent } = useHomeStore();

  const handleCheckboxChange = async (id) => {
    try {
      const selectedTodo = todos.find((todo) => todo.id === id);

      // 상태 업데이트: 로컬 상태에서 completed 토글
      const updatedTodos = todos.map((todo) =>
        todo.id === id ? { ...todo, completed: !todo.completed } : todo
      );
      setTodoData({ data: updatedTodos });

      // 서버에 완료 상태 전송 (id와 date 전달)
      await checkEvent(id, selectedTodo.date);
      console.log(`아이디 ${id} 토글 완료됨`);
    } catch (error) {
      console.error("토글 완료 에러:", error);

      // 오류 발생 시 상태 롤백
      const rolledBackTodos = todos.map((todo) =>
        todo.id === id ? { ...todo, completed: !todo.completed } : todo
      );
      setTodoData({ data: rolledBackTodos });
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
