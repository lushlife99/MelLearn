import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

function MockComment() {
  const navigate = useNavigate();
  const location = useLocation();
  const { comment } = location.state;
  console.log(comment);
  return <div>모의고사 해설</div>;
}

export default MockComment;
