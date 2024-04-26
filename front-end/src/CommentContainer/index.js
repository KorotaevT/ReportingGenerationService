import React, { useEffect, useState } from "react";
import ajax from "../Services/fetchService";
import { useUser } from "../UserProvider";
import { Button, Col, Row } from "react-bootstrap";
import Comment from "../Comment";
import dayjs from "dayjs";
import { useInterval } from "../util/useInterval";

const CommentContainer = (props) => {
  const { assignmentId } = props;
  const user = useUser();

  const emptyComment = {
    id: null,
    text: "",
    assignmentId: assignmentId != null ? parseInt(assignmentId) : null,
    user: user.jwt,
    createdDate: null,
  };
  const [comment, setComment] = useState(emptyComment);
  const [comments, setComments] = useState([]);

  useInterval(() => {
    updateCommentRelativeTime();
  }, 1000 * 5);

  function updateCommentRelativeTime() {
    const commentsCopy = [...comments];
    commentsCopy.forEach(
      (comment) => (comment.createdDate = dayjs(comment.createdDate))
    );
    setComments(commentsCopy);
  }

  function handleEditComment(commentId) {
    const i = comments.findIndex((comment) => comment.id === commentId);
    const commentCopy = {
      id: comments[i].id,
      text: comments[i].text,
      assignmentId: assignmentId != null ? parseInt(assignmentId) : null,
      user: user.jwt,
      createdDate: comments[i].createdDate,
    };
    setComment(commentCopy);
  }

  function handleDeleteComment(commentId) {
    const commentsCopy = [...comments];
    const i = commentsCopy.findIndex((comment) => comment.id === commentId);
    ajax(
      `/api/comments/${commentId}`,
      "DELETE",
      user.jwt,
      null
    ).then(() => {
      commentsCopy.splice(i, 1);
      setComments(commentsCopy);
    });
  }

  useEffect(() => {
    ajax(
      `/api/comments?assignmentId=${assignmentId}`,
      "GET",
      user.jwt,
      null
    ).then((commentsData) => {
      setComments(commentsData);
    });
  }, []);

  function updateComment(value) {
    const commentCopy = { ...comment };
    commentCopy.text = value;
    setComment(commentCopy);
  }

  function submitComment() {
    if (comment.id) {
      ajax(
        `/api/comments/${comment.id}`,
        "PUT",
        user.jwt,
        comment
      ).then((data) => {
          const commentsCopy = [...comments];
          const i = commentsCopy.findIndex((comment) => comment.id === data.id);
          commentsCopy[i] = data;
          setComments(commentsCopy);
          setComment(emptyComment);
      });
    } else {
      ajax(
        `/api/comments`,
        "POST",
        user.jwt,
        comment
      ).then((data) => {
          const commentsCopy = [...comments];
          commentsCopy.push(data);
          setComments(commentsCopy);
          setComment(emptyComment);
      });
    }
  }

  return (
    <>
      <div className="mt-5">
        <h4>Comments</h4>
      </div>
      <Row>
        <Col lg="8" md="10" sm="12">
          <textarea
            style={{ width: "100%", borderRadius: "0.25em" }}
            onChange={(e) => updateComment(e.target.value)}
            value={comment.text}
          ></textarea>
        </Col>
      </Row>
      <Button onClick={() => submitComment()}>Post Comment</Button>
      <div className="mt-5">
        {comments.map((comment) => (
          <Comment
            key={comment.id}
            commentData={comment}
            emitDeleteComment={handleDeleteComment}
            emitEditComment={handleEditComment}
          />
        ))}
      </div>
    </>
  );
};

export default CommentContainer;
