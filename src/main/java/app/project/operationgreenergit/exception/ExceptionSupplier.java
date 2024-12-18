package app.project.operationgreenergit.exception;

@FunctionalInterface
public interface ExceptionSupplier<T extends RuntimeException> {

	T get(String message, Throwable cause);

}
