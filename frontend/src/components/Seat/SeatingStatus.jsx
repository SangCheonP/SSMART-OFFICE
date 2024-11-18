import { PropTypes } from "prop-types";
import { memo, useMemo } from "react";

import styles from "@/styles/Seat/SeatingStatus.module.css";

const SeatingStatus = memo(({ floor, seats, totalNumber }) => {
  const occupantMap = useMemo(() => {
    return seats.reduce((acc, item) => {
      acc[item.info] = item;
      return acc;
    }, {});
  }, [seats]);

  const prefix = String.fromCharCode(65 + parseInt(floor, 10) - 1);
  const seatNumbers = Array.from(
    { length: totalNumber },
    (_, i) => `${prefix}${i + 1}`
  );

  return (
    <>
      <h2 className={styles.floor}>{floor}F</h2>
      <div className={styles.container}>
        {seatNumbers.map((seatNumber) => {
          const seatData = occupantMap[seatNumber];
          let statusClass = styles.vacant;
          if (seatData) {
            switch (seatData.status) {
              case "IN_USE":
                statusClass = styles.inUse;
                break;
              case "VACANT":
                statusClass = styles.vacant;
                break;
              case "UNAVAILABLE":
                statusClass = styles.unavailable;
                break;
              default:
                statusClass = styles.notOccupied;
                break;
            }
          }

          return (
            <div key={seatNumber} className={`${styles.seat} ${statusClass}`}>
              {seatData?.userId ? (
                <div className={styles.box}>
                  <div className={styles.role}>{seatData?.userPosition}</div>
                  <div className={styles.positionName}>
                    {seatData?.userDuty} {seatData?.userName}
                  </div>
                </div>
              ) : (
                <div className={styles.number}>{seatNumber}</div>
              )}
            </div>
          );
        })}
      </div>
    </>
  );
});

SeatingStatus.displayName = "SeatingStatus";

SeatingStatus.propTypes = {
  floor: PropTypes.string.isRequired,
  seats: PropTypes.array.isRequired,
  totalNumber: PropTypes.number.isRequired,
};

export default SeatingStatus;
