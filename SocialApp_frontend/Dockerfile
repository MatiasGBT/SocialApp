FROM node:16

RUN mkdir -p ~/SocialApp/Frontend

WORKDIR ~/SocialApp/Frontend

COPY package.json ./

RUN npm install

COPY . .

EXPOSE 4200

CMD ["npm", "start"]