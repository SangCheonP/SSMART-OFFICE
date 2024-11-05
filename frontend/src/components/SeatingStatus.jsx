import { PropTypes } from "prop-types";
import { memo, useMemo } from "react";

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
      <div>
        {seatNumbers.map((seatNumber) => {
          const seatData = occupantMap[seatNumber];

          return (
            <div
              key={seatNumber}
              style={{
                backgroundColor: seatData ? "lightblue" : "lightgray",
                padding: "10px",
                margin: "5px",
                borderRadius: "5px",
              }}
            >
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
};

export default SeatingStatus;
