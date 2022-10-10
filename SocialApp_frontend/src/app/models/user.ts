import { Post } from "./post";

export class User {
    idUser: number;
    username: string;
    name: string;
    surname: string;
    photo: string;
    description: string;
    creationDate: Date;
    deletionDate: Date;
    isFriend: boolean;
    isChecked: boolean;
    isConnected: boolean;
}