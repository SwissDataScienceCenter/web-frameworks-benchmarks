const express = require('express');
const https = require('https');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();

const authMiddleware = function(req, res, next) {
  https.get("https://www.google.com/", res => {
    if (res.statusCode < 200 || res.statusCode >= 300) {
      throw new Error('Unauthorized')
    }
    next()
  })
}
app.use('/', authMiddleware)
app.use('/', createProxyMiddleware({ target: 'http://flask:3000', changeOrigin: true }));
app.listen(3000);
