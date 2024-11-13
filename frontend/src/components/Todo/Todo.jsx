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
import { addCalendarEvent } from "@/services/homeApi";

const Todo = ({ selectedDate, monthData }) => {
  // 선택된 날짜에 해당하는 일정 필터링
  const filteredDate = monthData.filter(
    (item) => item.date === dayjs(selectedDate).format("YYYY-MM-DD")
  );
  const openModal = useModalStore((state) => state.openModal);

  const handleAddTodoClick = () => {
    openModal(AddTodoModal, {
      onSubmit: async ({
        assignmentName,
        assignmentDate,
        assignmentType,
        description,
      }) => {
        try {
          await addCalendarEvent(
            assignmentName,
            assignmentDate,
            assignmentType,
            description
          );
          console.log("일정 추가 성공");

          // 여기에서 새롭게 추가된 데이터를 다시 가져오거나, 추가된 일정만 반영하도록 설정할 수 있습니다.
          // 예: 추가된 일정을 monthData에 추가하거나 다시 fetch하여 최신 데이터로 업데이트
        } catch (error) {
          console.error("일정 추가 실패:", error);
        }
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
