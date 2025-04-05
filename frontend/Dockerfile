FROM node:18-alpine3.21 AS builder

WORKDIR /app

COPY package.json package-lock.json* ./

RUN npm install 

COPY . .

EXPOSE 5173

RUN npm run build

# nginx

FROM nginx:stable-alpine3.19-slim

WORKDIR /usr/share/nginx/html

COPY --from=builder /app/dist .

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD [ "nginx", "-g", "daemon off;" ]