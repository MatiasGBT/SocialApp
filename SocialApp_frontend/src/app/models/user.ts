import { Post } from "./post";

export class User {
    idUser: number;
    username: string;
    name: string;
    surname: string;
    photo: string;
    description: string;
    posts: Post[];
    creationDate: Date;
    deletionDate: Date;
    isFriend: boolean;
}