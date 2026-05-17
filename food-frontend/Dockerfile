# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app

# Install deps first (cached layer)
COPY package*.json ./
RUN npm install --no-audit --prefer-offline --legacy-peer-deps

# Bake the backend URL in at build time (CRA inlines REACT_APP_* vars)
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL

COPY . .
RUN npm run build

# ---- Runtime stage ----
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
