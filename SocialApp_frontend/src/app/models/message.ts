import { User } from "./user";

export class Message {
    idMessage: number;
    text: string;
    userTransmitter: User;
    userReceiver: User;
    date: Date;
    photo: string;
}
