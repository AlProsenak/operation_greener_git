package app.project.operationgreenergit.exception;

@FunctionalInterface
public interface ExceptionMessageSupplier<T extends RuntimeException> {

	T get(String message);

}
