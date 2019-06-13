package chatbot.teamcity.model;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class UserKey {
	
	private String chatClientType;
	private String chatClientGroup;
	private String chatUserName;
	
	public String getMappingKey() {
		return getChatClientType() + ":" + getChatClientGroup() + ":" + getChatUserName();
	}
	
	public Errors validate() {
		Validator v = new UserKeyValidator();
		Errors e = new BeanPropertyBindingResult(this, "userKey");
		v.validate(this, e);
		return e;
	}
	
	private static class UserKeyValidator implements Validator {

		@Override
		public boolean supports(Class<?> clazz) {
			return UserKey.class.isAssignableFrom(clazz);
		}

		@Override
		public void validate(Object target, Errors errors) {
		       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "chatClientType", "field.required");
		       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "chatClientGroup", "field.required");
		       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "chatUserName", "field.required");
		}
		
	}
}