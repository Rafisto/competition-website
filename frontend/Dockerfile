FROM node:14 AS build

WORKDIR .

COPY package*.json ./
COPY . .

RUN npm install
RUN npm run build

FROM nginx:alpine

COPY --from=build ./build /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf

COPY nginx-custom.conf /etc/nginx/conf.d/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
