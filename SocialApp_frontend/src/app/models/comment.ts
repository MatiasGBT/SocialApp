import { User } from "./user";

export class Comment {
    idComment: number;
    text: string;
    user: User;
    answers: Comment[];
}
