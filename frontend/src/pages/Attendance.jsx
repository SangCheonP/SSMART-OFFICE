import React from "react";
import MemberList from "@/components/Message/MemberList";
import styles from "@/styles/Attendance/Member.module.css";
const Attendance = () => {
  return (
    <div>
      <div className={styles}></div>
      <MemberList memberData={memberData} onMemberSelect={handleMemberSelect} />
    </div>
  );
};

export default Attendance;
