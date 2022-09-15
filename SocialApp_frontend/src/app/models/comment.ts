import { Post } from "./post";
import { User } from "./user";

export class Comment {
    idComment: number;
    text: string;
    user: User;
    post: Post;
    answers: Comment[];
    date: Date;
    answersQuantity: number;
}
