import React from "react";
import styles from "../../styles/Home/Todo.module.css";
import Month from "../../assets/Todo/Todo.svg?react";
import FileIcon from "../../assets/Todo/File.svg?react";
import MessageIcon from "../../assets/Todo/message.svg?react";
import ReadCard from "./ReadCard";
import TodoList from "./TodoList";
import AddButton from "../../assets/Todo/AddButton.svg?react";

const Todo = () => {
  return (
    <div>
      <section className={styles.todo_box}>
        <div className={styles.contentWrapper}>
          <header className={styles.name_tag}>
            <Month className={styles.month} />
            <span className={styles.name}>복현우</span>
            <span className={styles.name_text}>님의 일과</span>
          </header>

          <ReadCard
            icon={<FileIcon />}
            iconBgColor="#ffebeb"
            title="받은 파일"
            count={41}
            linkText="View All"
            linkUrl="#"
          />
          <ReadCard
            icon={<MessageIcon />}
            iconBgColor="#FFFBCF"
            title="읽지 않은 메시지"
            count={32}
            linkText="View All"
            linkUrl="#"
          />

          <TodoList />

          {/* 일정 추가 버튼 클릭 시 모달 띄우기 */}
          <button
            className={styles.add_todo}
            onClick={() => alert("일정을 추가하시겠습니까?")}
          >
            <div className={styles.add_inner}>
              <AddButton />
              <span className={styles.add_text}>일정 추가</span>
            </div>
          </button>
        </div>
      </section>
    </div>
  );
};

export default Todo;
