# Our first FROM statement declares the build environment.
FROM rust AS builder

# Add our source code.
COPY src ./src
COPY Cargo.* ./

# Build our application.
RUN cargo build --release

CMD ["target/release/rust-benchmark"]
