import React, { useEffect, useState } from "react";
import { useUser } from "../UserProvider";
import { jwtDecode } from "jwt-decode";
import dayjs, { Dayjs } from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";

const Comment = (props) => {
  const user = useUser();
  const decodedJwt = jwtDecode(user.jwt);
  const { id, createdDate, createdBy, text } = props.commentData;
  const { emitEditComment, emitDeleteComment } = props;
  const [commentRelativeTime, setCommentRelativeTime] = useState("");
  
  useEffect(() => {
    updateCommentRelativeTime();
  }, [createdDate]);

  function updateCommentRelativeTime() {
    if (createdDate) {
      dayjs.extend(relativeTime);
      setCommentRelativeTime(dayjs(createdDate).fromNow());
    }
  }

  return (
    <>
      <div className="comment-bubble">
        <div className="d-flex gap-5" style={{ fontWeight: "bold" }}>
          <div>{`${createdBy.username}`}</div>
          {decodedJwt.sub === createdBy.username ? (
            <>
              <div
                onClick={() => emitEditComment(id)}
                style={{ cursor: "pointer", color: "blue" }}
              >
                edit
              </div>
              <div
                onClick={() => emitDeleteComment(id)}
                style={{ cursor: "pointer", color: "red" }}
              >
                delete
              </div>
            </>
          ) : (
            <></>
          )}
        </div>
        <div>{text}</div>
      </div>

      <div
        style={{ marginTop: "-1.25em", marginLeft: "1.4em", fontSize: "12px" }}
      >
        {commentRelativeTime ? `Posted ${commentRelativeTime}` : ""}
      </div>
    </>
  );
};

export default Comment;
