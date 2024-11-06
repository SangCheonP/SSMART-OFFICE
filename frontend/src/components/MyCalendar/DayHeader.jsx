// DayHeader.js
import React from "react";
import { format } from "date-fns";

const DayHeader = ({ date, label }) => {
  const shortLabel = format(date, "EEE");
  return <div>{shortLabel}</div>;
};

export default DayHeader;
