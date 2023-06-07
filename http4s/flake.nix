{
  description = "http4s-test-app project flake";
  inputs = {
    nixpkgs.url = "nixpkgs/nixos-22.11";
    sbt.url = "github:zaninime/sbt-derivation";
    sbt.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, sbt }:
    let
      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
      nixpkgsFor = forAllSystems (system: import nixpkgs { inherit system; });
    in
    rec
    {
      overlays.default = final: prev: {
        http4s-test-app = with final; sbt.lib.mkSbtDerivation {
          pkgs = final;

          version = "dynamic";
          pname = "http4s-test-app";

          src = lib.sourceByRegex ./. [
            "^build.sbt$"
            "^src$"
            "^src/.*$"
            "^project$"
            "^project/.*$"
          ];

          depsSha256 = "sha256-EYsuVld0y1SimiAeYuG63l21LzglcU3VhcGzZGBz4pE=";

          buildPhase = ''
            sbt Universal/stage
          '';

          installPhase = ''
            mkdir -p $out
            cp -R target/universal/stage/* $out/

            cat > $out/bin/http4s-test <<-EOF
            #!${bash}/bin/bash
            $out/bin/http4s-test-app -java-home ${jdk} "\$@"
            EOF
            chmod 755 $out/bin/http4s-test
          '';
        };
      };

      packages = forAllSystems (system:
        {
          default = (import nixpkgs {
            inherit system;
            overlays = [ self.overlays.default ];
          }).http4s-test-app;
        });


      devShells = forAllSystems(system:
        { default =
            let
              pkgs = import nixpkgs { inherit system; };
            in
              pkgs.mkShell {
                buildInputs = [
                  pkgs.sbt
                  pkgs.openjdk
                  pkgs.graalvm17-ce
                ];
                nativeBuildInputs =
                  [
                  ];
              };
        });
    };
}
