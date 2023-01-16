FROM node:18
WORKDIR /app
COPY index.js package.json package-lock.json ./
RUN npm install
ENTRYPOINT [ "node", "index.js" ]
