import React from "react";
import MemberList from "../components/Message/MemberList";
import styles from "./../styles/Message/Message.module.css";
import AddMember from "./../assets/Message/AddMember.svg?react";

const Message = () => {
  return (
    <div className={styles.message_container}>
      <div className={styles.member_box}>
        <AddMember className={styles.add_member} />
        <MemberList />
      </div>
    </div>
  );
};

export default Message;
