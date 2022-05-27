FROM tobiasgaenzler/math-pyramid
# TODO: build image in this file instead of using prebuild docker image
# TODO: execute with non-root user
EXPOSE 80

ENTRYPOINT ["java","-cp","@/app/jib-classpath-file","de.tobiasgaenzler.mathpyramid.MathPyramidApplication"]