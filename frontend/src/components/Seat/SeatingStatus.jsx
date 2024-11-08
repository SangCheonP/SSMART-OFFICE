import { PropTypes } from "prop-types";
import { memo, useMemo } from "react";

import styles from "@/styles/Seat/SeatingStatus.module.css";

const SeatingStatus = memo(({ floor, occupant, totalNumber }) => {
  const occupantMap = useMemo(() => {
    return occupant.reduce((acc, item) => {
      acc[item.number] = item;
      return acc;
    }, {});
  }, [occupant]);

  const seatNumbers = Array.from({ length: totalNumber }, (_, i) => i + 1);

  return (
    <>
      <h2>{floor}F</h2>
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
              {seatData ? (
                <>
                  <div>{seatData.name}</div>
                  <div>{seatData.position}</div>
                  <div>{seatData.role}</div>
                </>
              ) : (
                <div>{seatNumber}</div>
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
  occupant: PropTypes.array.isRequired,
  totalNumber: PropTypes.number.isRequired,
};

export default SeatingStatus;
