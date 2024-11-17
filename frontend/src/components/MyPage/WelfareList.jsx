import styles from "@/styles/MyPage/WelfareList.module.css";
import WelfareDatePicker from "@/components/common/WelfareDatePicker";
import { useEffect, useState } from "react";
import { fetchMyWelfarePointList } from "@/services/myInfoAPI";
import dayjs from "dayjs";
import "dayjs/locale/ko";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import Pagination from "@/components/common/Pagination";

import Swal from "sweetalert2";

dayjs.locale("ko");
dayjs.extend(utc);
dayjs.extend(timezone);

const WelfareList = () => {
  const [welfarePointList, setWelfarePointList] = useState([]);
  const [startDate, setStartDate] = useState(
    new Date(new Date().setDate(new Date().getDate() - 7))
  );
  const [endDate, setEndDate] = useState(new Date());
  const [page, setPage] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchWelfarePointListWithPage(page);
  }, [page]);

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const fetchWelfarePointListWithPage = (newPage) => {
    fetchMyWelfarePointList({
      startDate: dayjs(startDate).format("YYYY-MM-DD"),
      endDate: dayjs(endDate).format("YYYY-MM-DD"),
      page: newPage - 1,
    }).then((res) => {
      setWelfarePointList(res.data.content);
      setTotalElements(res.data.totalElements);
      setTotalPages(res.data.totalPages);
    });
  };

  const formatWithCommas = (num) =>
    num?.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") || 0;

  const handleListClick = () => {
    if (startDate > endDate) {
      Swal.fire({
        icon: "error",
        title: "날짜 입력 오류",
        text: "시작 날짜는 종료 날짜보다 이전이어야 합니다.",
        showConfirmButton: false,
        timer: 1500,
      });
      setStartDate(new Date(new Date().setDate(new Date().getDate() - 7)));
      setEndDate(new Date());
      return;
    }
    // 검색 시 1페이지로 초기화
    fetchWelfarePointListWithPage(1);
  };

  return (
    <>
      <div className={styles.box}>
        <div className={styles.title}>
          복지 포인트 내역 (총 {formatWithCommas(totalElements)}건)
        </div>
        <div className={styles.dateBox}>
          <WelfareDatePicker
            value={startDate}
            onChange={(date) => setStartDate(date)}
          />
          <p style={{ color: "var(--blue)" }}>~</p>
          <WelfareDatePicker
            value={endDate}
            onChange={(date) => setEndDate(date)}
          />
          <button className={styles.welfareSearch} onClick={handleListClick}>
            검색
          </button>
        </div>
      </div>
      <div className={styles.table}>
        <div className={styles.tableHeader}>
          <div className={styles.first}>사용처</div>
          <div>상세</div>
          <div>수량</div>
          <div>날짜</div>
          <div>결제 금액</div>
          <div>잔여</div>
        </div>
        {welfarePointList.length > 0 ? (
          welfarePointList.map((item) => (
            <div key={item.id} className={styles.content}>
              <div className={styles.first}>{item.marketName}</div>
              <div>{item.item || "-"}</div>
              <div>{item.quantity || "-"}</div>
              <div>
                {dayjs(item.transactionTime).format("YYYY.MM.DD HH:mm")}
              </div>
              <div
                className={Number(item.amount) < 0 ? styles.minus : styles.plus}
              >
                {formatWithCommas(item.amount)}
              </div>
              <div>{formatWithCommas(item.balance)}</div>
            </div>
          ))
        ) : (
          <div className={styles.none}>
            <div>사용 내역이 존재하지 않습니다</div>
          </div>
        )}
      </div>
      <Pagination
        totalPages={totalPages}
        currentPage={page}
        onPageChange={handlePageChange}
      />
    </>
  );
};

export default WelfareList;
