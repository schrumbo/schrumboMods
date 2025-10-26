{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk21
    gradle
  ];

  shellHook = ''
    export JAVA_HOME=${pkgs.jdk21}
  '';
}