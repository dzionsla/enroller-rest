package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeetingService meetingService;

	@MockBean
	private ParticipantService participantService;

	@Test
	public void getParticipants() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		Collection<Participant> allParticipants = singletonList(participant);
		given(participantService.getAll()).willReturn(allParticipants);

		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}
	
	@Test
	public void getParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		given(participantService.findByLogin(participant.getLogin())).willReturn(participant);

		mvc.perform(get("/participants/testlogin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(new ObjectMapper().writeValueAsString(participant)));
	}

	@Test
	public void addParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		given(participantService.findByLogin(participant.getLogin())).willReturn(null);
		given(participantService.add(participant)).willReturn(participant);

		mvc.perform(post("/participants").content(new ObjectMapper().writeValueAsString(participant)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(content().string(new ObjectMapper().writeValueAsString(participant)));
	}
	
	@Test
	public void addExistingParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("");
		participant.setPassword("testpassword");
		
		String response = "Unable to create. A participant with login " + participant.getLogin() + " already exist.";

		given(participantService.findByLogin(participant.getLogin())).willReturn(null);

		mvc.perform(post("/participants").content(new ObjectMapper().writeValueAsString(participant)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
				.andExpect(content().string(response));
		//verify(participantService, never()).add(participant);
	}
	
	
	@Test
	public void delExistingParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		
		//given(participantService.add(participant)).willReturn(participant);
		//given(participantService.delete(participant)).willReturn(null);
		Mockito.doNothing().when(participantService).delete(participant);

		mvc.perform(delete("/participants/testlogin").content(new ObjectMapper().writeValueAsString(participant)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent())
			.andExpect(content().string(new ObjectMapper().writeValueAsString(participant)));
	}
	
}
