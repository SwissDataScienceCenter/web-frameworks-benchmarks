use actix_web::{get, web, App, HttpServer, Responder, Result};
use serde::Serialize;
extern crate redis;
use bb8_redis::{bb8::Pool, redis::AsyncCommands, RedisConnectionManager};
use std::time::Duration;

pub type BB8Pool = Pool<RedisConnectionManager>;

#[derive(Serialize)]
struct MessageResponse {
    message: String,
}

#[derive(Serialize)]
struct ValuesResponse {
    values: Vec<String>,
}

pub async fn create_pool(host_addr: &str) -> BB8Pool {
    let manager =
        RedisConnectionManager::new(host_addr).expect("couldn't create pool");
    Pool::builder()
        .max_size(30)
        .connection_timeout(Duration::from_secs(10))
        .build(manager)
        .await
        .expect("couldn't get pool")
}

pub async fn get(pool: &BB8Pool, key: &str) -> String {
    let mut con = pool
        .get()
        .await
        .expect("couldn't open connection");
    con.get(key)
        .await
        .expect("couldn't get value")
}

#[get("/")]
async fn hello() -> Result<impl Responder> {
    let obj = MessageResponse {
        message: "Hello world".to_string(),
    };
    Ok(web::Json(obj))
}

#[get("/redis")]
async fn redis_call(pool: web::Data<BB8Pool>) -> Result<impl Responder> {
    let output: Vec<String> = vec![
        get(&pool, "test1").await,
        get(&pool, "test2").await,
        get(&pool, "test3").await,
    ];

    let obj = ValuesResponse {values: output};
    Ok(web::Json(obj))
}


#[actix_web::main]
async fn main() -> std::io::Result<()> {
    println!("Starting server.");
    let bb8_pool = create_pool("redis://redis").await;
    HttpServer::new(move|| {
        App::new()
            .service(hello)
            .service(redis_call)
            .app_data(web::Data::new(bb8_pool.clone()))
    })
    .bind(("0.0.0.0", 3000))?
    .run()
    .await
}
