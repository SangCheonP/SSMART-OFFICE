import React from "react";
import styles from "@/styles/Home/Todo.module.css";
import Month from "@/assets/Todo/Todo.svg?react";
import FileIcon from "@/assets/Todo/File.svg?react";
import MessageIcon from "@/assets/Todo/Message.svg?react";
import ReadCard from "./ReadCard";
import TodoList from "./TodoList";
import AddButton from "@/assets/Todo/AddButton.svg?react";
import dayjs from "dayjs";
import useModalStore from "@/store/useModalStore";
import AddTodoModal from "../Modals/AddTodoModal";

const Todo = ({ selectedDate, monthData }) => {
  // 선택된 날짜에 해당하는 일정 필터링
  const filteredDate = monthData.filter(
    (item) => item.date === dayjs(selectedDate).format("YYYY-MM-DD")
  );
  const openModal = useModalStore((state) => state.openModal);

  const handleAddTodoClick = () => {
    openModal(AddTodoModal, {
      onsubmit: () => {
        console.log("일정추가 모달");
      },
    });
  };

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

          <TodoList monthData={filteredDate} />

          {/* 일정 추가 버튼 클릭 시 모달 띄우기 */}
          <button className={styles.add_todo} onClick={handleAddTodoClick}>
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
