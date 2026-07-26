package com.github.exaxl.wms.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;
import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;
import com.github.exaxl.wms.domain.enums.BinErrorCode;
import com.github.exaxl.wms.domain.enums.MovementErrorCode;
import com.github.exaxl.wms.domain.enums.ProductErrorCode;
import com.github.exaxl.wms.domain.enums.WarehouseErrorCode;

class ExceptionErrorCodeMappingTest {

	private static final String MAIN_PACKAGE = "com.github.exaxl.wms";

	@ParameterizedTest(name = "{0} should have error code {1}")
	@MethodSource("exceptionToErrorCodeMappings")
	void exception_ShouldHaveExpectedErrorCode(ErrorCodeException exception, ErrorCode expectedErrorCode) {
		assertThat(exception.getErrorCode()).isEqualTo(expectedErrorCode);
	}

	@ParameterizedTest(name = "{0} - all constructors should preserve error code")
	@MethodSource("exceptionClassesUnderTest")
	void allConstructors_ShouldPreserveErrorCodeAndPropagateArgs(Class<? extends ErrorCodeException> exceptionClass)
			throws Exception {
		ErrorCode expectedCode = exceptionClassToErrorCodeMap().get(exceptionClass);
		assertThat(expectedCode).as("no mapping found for %s — add it to exceptionToErrorCodeMappings()",
				exceptionClass.getSimpleName()).isNotNull();

		// no-arg
		ErrorCodeException noArg = exceptionClass.getDeclaredConstructor().newInstance();
		assertThat(noArg.getErrorCode()).isEqualTo(expectedCode);

		// (String message)
		ErrorCodeException withMessage = exceptionClass.getDeclaredConstructor(String.class).newInstance("msg");
		assertThat(withMessage.getErrorCode()).isEqualTo(expectedCode);
		assertThat(withMessage.getMessage()).isEqualTo("msg");

		// (Throwable cause)
		Throwable cause = new RuntimeException("cause");
		ErrorCodeException withCause = exceptionClass.getDeclaredConstructor(Throwable.class).newInstance(cause);
		assertThat(withCause.getErrorCode()).isEqualTo(expectedCode);
		assertThat(withCause.getCause()).isEqualTo(cause);

		// (String message, Throwable cause)
		ErrorCodeException withBoth = exceptionClass.getDeclaredConstructor(String.class, Throwable.class)
				.newInstance("msg", cause);
		assertThat(withBoth.getErrorCode()).isEqualTo(expectedCode);
		assertThat(withBoth.getMessage()).isEqualTo("msg");
		assertThat(withBoth.getCause()).isEqualTo(cause);
	}

	@Test
	void allExceptionClasses_ShouldBeRegisteredInMapping() {
		Set<Class<?>> allExceptionClasses = scanExceptionClasses();

		Set<Class<?>> registeredClasses = exceptionToErrorCodeMappings().map(args -> args.get()[0].getClass())
				.collect(Collectors.toSet());

		Set<Class<?>> missingFromMapping = new HashSet<>(allExceptionClasses);
		missingFromMapping.removeAll(registeredClasses);

		assertThat(missingFromMapping).as(
				"following exceptions extend ErrorCodeException (directly or not) but they are not registred in exceptionToErrorCodeMappings()")
				.isEmpty();
	}

	private Set<Class<?>> scanExceptionClasses() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(ErrorCodeException.class));

		return scanner.findCandidateComponents(MAIN_PACKAGE).stream().map(BeanDefinition::getBeanClassName)
				.map(this::loadClass).collect(Collectors.toSet());
	}

	private Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load class " + className, e);
		}
	}

	private static Stream<Arguments> exceptionToErrorCodeMappings() {
		return Stream.of(Arguments.of(new BinNotFoundException("msg"), BinErrorCode.BIN_NOT_FOUND),
				Arguments.of(new BinZoneIncompatibleException("msg"), BinErrorCode.BIN_ZONE_INCOMPATIBLE),
				Arguments.of(new DuplicateMovementException("msg"), MovementErrorCode.MOVEMENT_DUPLICATED),
				Arguments.of(new ProductNotAllowedException("msg"), ProductErrorCode.PRODUCT_NOT_ALLOWED),
				Arguments.of(new ProductNotFoundException("msg"), ProductErrorCode.PRODUCT_NOT_FOUND),
				Arguments.of(new WarehouseNotActiveException("msg"), WarehouseErrorCode.WAREHOUSE_NOT_ACTIVE),
				Arguments.of(new WarehouseNotFoundException("msg"), WarehouseErrorCode.WAREHOUSE_NOT_FOUND),
				Arguments.of(new MessageValidationException("msg"), MessageErrorCode.MESSAGE_VALIDATION_FAILED));
	}

	@SuppressWarnings("unchecked")
	private static Stream<Class<? extends ErrorCodeException>> exceptionClassesUnderTest() {
		return exceptionClassToErrorCodeMap().keySet().stream().map(c -> (Class<? extends ErrorCodeException>) c);
	}

	private static Map<Class<?>, ErrorCode> exceptionClassToErrorCodeMap() {
		return exceptionToErrorCodeMappings()
				.collect(Collectors.toMap(args -> args.get()[0].getClass(), args -> (ErrorCode) args.get()[1]));
	}

}
