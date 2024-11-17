import PropTypes from "prop-types";
import React, { useState, useEffect } from "react";
import ReactModal from "react-modal";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import styles from "@/styles/Modals/AddTodoModal.module.css";
import useHomeStore from "@/store/useHomeStore";
import Down from "@/assets/Common/Arrow_down.svg?react";

import { handleError } from "@/utils/errorHandler";
import { handleSuccess } from "@/utils/successHandler";

const AddTodoModal = ({ onSubmit, onClose }) => {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [assignmentName, setAssignmentName] = useState("");
  const [assignmentType, setAssignmentType] = useState("연차");
  const [description, setDescription] = useState("");
  const [isSubmitEnabled, setIsSubmitEnabled] = useState(false);

  const addCalendarEvent = useHomeStore((state) => state.addCalendarEvent);

  useEffect(() => {
    const isFormValid =
      assignmentName.trim() !== "" && description.trim() !== "" && selectedDate;
    setIsSubmitEnabled(isFormValid);
  }, [assignmentName, description, selectedDate]);

  const typeChange = (type) => {
    switch (type) {
      case "연차":
        return "ANNUAL_LEAVE";
      case "조퇴":
        return "EARLY_LEAVE";
      case "회의":
        return "MEETING";
      case "할일":
        return "TASK";
      default:
        return "OTHER";
    }
  };

  // 완료 버튼
  const handleClickSubmit = async () => {
    if (isSubmitEnabled) {
      const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
      };

      const dataToSubmit = {
        name: assignmentName,
        date: formatDate(selectedDate),
        type: typeChange(assignmentType),
        description: description,
      };
      console.log("데이터 AddTodoModal:", dataToSubmit);

      try {
        await addCalendarEvent(
          dataToSubmit.name,
          dataToSubmit.date,
          dataToSubmit.type,
          dataToSubmit.description
        );
        handleSuccess("일정 추가 성공!");

        onClose();
      } catch (error) {
        handleError(error);
      }
    }
  };

  // 닫기 버튼
  const handleClickCancel = () => {
    onClose();
  };

  return (
    <ReactModal
      isOpen={true}
      style={{
        overlay: {
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: "rgba(0, 0, 0, 0.75)",
          zIndex: 1000,
        },
        content: {
          position: "absolute",
          width: "400px",
          height: "550px",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          background: "#fff",
          overflow: "hidden",
          WebkitOverflowScrolling: "touch",
          borderRadius: "20px",
          outline: "none",
          padding: "20px",
        },
      }}
    >
      <div className={styles.container}>
        <div className={styles.datePickerContainer}>
          <DatePicker
            selected={selectedDate}
            onChange={(date) => setSelectedDate(date)}
            dateFormat="yyyy년 MM월 dd일"
            className={styles.datePicker}
            popperPlacement="bottom-end"
          />
          <Down />
        </div>
        <h1 className={styles.titleBox}>일정 유형</h1>
        <div>
          <select
            className={styles.select}
            value={assignmentType}
            onChange={(e) => setAssignmentType(e.target.value)}
          >
            <option>연차</option>
            <option>조퇴</option>
            <option>회의</option>
            <option>할일</option>
            <option>기타</option>
          </select>
        </div>
        <div className={styles.titleBox}>
          <h1 className={styles.title}>일정 이름</h1>
          <h1 className={styles.titlePoint}>*</h1>
        </div>
        <input
          type="text"
          value={assignmentName}
          onChange={(e) => setAssignmentName(e.target.value)}
          className={`${styles.input} ${
            !assignmentName.trim() ? styles.inputError : ""
          }`}
          placeholder="일정 이름을 입력해주세요."
          required
        />

        <div>
          <div className={styles.titleBox}>
            <h1 className={styles.title}>설명</h1>
            <h1 className={styles.titlePoint}>*</h1>
          </div>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className={`${styles.textarea} ${
              !description.trim() ? styles.inputError : ""
            }`}
            placeholder="일정에 대해 설명해주세요."
            required
          ></textarea>
        </div>

        <div className={styles.buttonBox}>
          <button
            onClick={handleClickCancel}
            className={`${styles.cancel} ${styles.button}`}
          >
            취소
          </button>
          <button
            onClick={handleClickSubmit}
            className={`${styles.confirm} ${styles.button}`}
            disabled={!isSubmitEnabled} // 모든 필드가 입력되지 않으면 버튼 비활성화 TODO 에러메시지 띄우기
          >
            완료
          </button>
        </div>
      </div>
    </ReactModal>
  );
};

AddTodoModal.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default AddTodoModal;
