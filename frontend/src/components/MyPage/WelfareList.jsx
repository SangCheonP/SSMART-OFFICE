import styles from "@/styles/MyPage/WelfareList.module.css";
import WelfareDatePicker from "@/components/common/WelfareDatePicker";
import { useEffect, useState } from "react";
import { fetchMyWelfarePointList } from "@/services/myInfoAPI";

import dayjs from "dayjs";
import "dayjs/locale/ko";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
import Pagination from "@/components/common/Pagination";

dayjs.locale("ko");
dayjs.extend(utc);
dayjs.extend(timezone);

const data = {
  content: [
    {
      id: 21, //거래 아이디
      marketName: "삼성화재 연수원", //지급처
      amount: 300000, //지급 금액
      balance: 300000, //잔액
      transactionTime: "2024-11-09T18:00:00", //거래 시간
      //item과 quantity는 null이면 오지 않음
    },
    {
      id: 22,
      marketName: "구내식당 1층", //사용처
      amount: -12000, //지급 금액
      balance: 288000, //잔액
      transactionTime: "2024-11-09T21:10:00",
      item: "점심 식사", //상세 항목
      quantity: 1, //수량
    },
    {
      id: 23,
      marketName: "구내식당 2층",
      amount: -13500,
      balance: 274500,
      transactionTime: "2024-11-10T21:30:00",
      item: "점심 특선",
      quantity: 1,
    },
    {
      id: 24,
      marketName: "카페 바리스타",
      amount: -5000,
      balance: 269500,
      transactionTime: "2024-11-10T23:20:00",
      item: "라떼",
      quantity: 1,
    },
    {
      id: 25,
      marketName: "문구점",
      amount: -12500,
      balance: 257000,
      transactionTime: "2024-11-11T01:00:00",
      item: "사무용품",
      quantity: 1,
    },
    {
      id: 26,
      marketName: "구내식당 1층",
      amount: -11500,
      balance: 245500,
      transactionTime: "2024-11-11T21:00:00",
      item: "점심 식사",
      quantity: 1,
    },
    {
      id: 27,
      marketName: "카페테리아",
      amount: -5500,
      balance: 240000,
      transactionTime: "2024-11-12T00:45:00",
      item: "카페모카",
      quantity: 1,
    },
    {
      id: 28,
      marketName: "편의점",
      amount: -7000,
      balance: 233000,
      transactionTime: "2024-11-12T03:30:00",
      item: "간식",
      quantity: 2,
    },
    {
      id: 29,
      marketName: "구내식당 2층",
      amount: -12500,
      balance: 220500,
      transactionTime: "2024-11-12T21:20:00",
      item: "점심 식사",
      quantity: 1,
    },
    {
      id: 30,
      marketName: "카페 바리스타",
      amount: -4500,
      balance: 216000,
      transactionTime: "2024-11-12T23:15:00",
      item: "아메리카노",
      quantity: 1,
    },
  ],
  pageable: {
    pageNumber: 0,
    pageSize: 10,
    sort: [
      {
        direction: "ASC",
        property: "id",
        ignoreCase: false,
        nullHandling: "NATIVE",
        ascending: true,
        descending: false,
      },
    ],
    offset: 0,
    paged: true,
    unpaged: false,
  },
  last: false,
  totalPages: 2,
  totalElements: 18,
  first: true,
  size: 10,
  number: 0,
  sort: [
    {
      direction: "ASC",
      property: "id",
      ignoreCase: false,
      nullHandling: "NATIVE",
      ascending: true,
      descending: false,
    },
  ],
  numberOfElements: 10,
  empty: false,
};

const WelfareList = () => {
  const [welfarePointList, setWelfarePointList] = useState([]);
  const [startDate, setStartDate] = useState(
    new Date(new Date().setDate(new Date().getDate() - 7))
  );
  const [endDate, setEndDate] = useState(new Date());
  const [page, setPage] = useState(1);
  const [totalElements, setTotalElements] = useState(10);
  const pageSize = 10; // 페이지 당 항목 수
  const totalPages = Math.ceil(totalElements / pageSize);

  useEffect(() => {
    // API 호출 시 페이지와 날짜 범위를 반영
    const fetchData = async () => {
      // 예제 데이터를 사용
      const startIndex = (page - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      setWelfarePointList(data.content.slice(startIndex, endIndex));
      setTotalElements(data.totalElements);
    };
    fetchData();
  }, [page, startDate, endDate]);

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const formatWithCommas = (num) => {
    return num?.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") || 0;
  };

  return (
    <>
      <div className={styles.title}>복지 포인트 내역</div>
      <div className={styles.table}>
        <div className={styles.dateBox}>
          <WelfareDatePicker
            value={startDate}
            onChange={(date) => setStartDate(date)}
          />
          <p>~</p>
          <WelfareDatePicker
            value={endDate}
            onChange={(date) => setEndDate(date)}
          />
          <button className={styles.welfareSearch}>검색</button>
        </div>
        <div className={styles.tableHeader}>
          <div className={styles.first}>사용처</div>
          <div>상세</div>
          <div>수량</div>
          <div>날짜</div>
          <div>결제 금액</div>
          <div>잔여</div>
        </div>
        {welfarePointList?.map((item) => {
          return (
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
          );
        })}
        <Pagination
          totalPages={totalPages}
          currentPage={page}
          onPageChange={handlePageChange}
        />
      </div>
    </>
  );
};

export default WelfareList;
