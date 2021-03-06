package com.cg.flight.services;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cg.flight.dao.ITicketDao;
import com.cg.flight.entities.Ticket;
import com.cg.flight.entities.User;
import com.cg.flight.exceptions.InvalidRequestException;
import com.cg.flight.exceptions.NotFound;

@Service
public class TicketService implements ITicketService{
	
	private static Logger logger  = LoggerFactory.getLogger(TicketService.class);
	
	@Autowired
	private ITicketDao ticketRepo;
	
	@Autowired
	private IUserService userService;
	
	

	/*
	 * Function To Cancel Ticket owned by User identified By username.
	 * Can Throw Invalid TicketException, Or TicketNotFoundException.
	 * On Success the ticket gets Cancelled.
	 * */

	@Override
	@Transactional
	public boolean cancelTicket(int ticketId, String username) throws Exception {
		Ticket ticket = this.findTicketById(ticketId);
		if (!(ticket.getUser().getUsername().equals(username)))
		{
			logger.error("Given ticket with ticket Id : " + ticketId + " is Invalid");
			throw new InvalidRequestException("Given ticket with ticket Id : " + ticketId + " is Invalid");
		}
		if(ticket.getStatus().equals("Cancelled"))
		{
			logger.error("Given ticket with ticket Id : " + ticketId + " is already cancelled");
			throw new InvalidRequestException("Given ticket with ticket Id : " + ticketId + " is already cancelled");
		}
		
		return ticketRepo.cancelTicket(ticket);
	}
	

	/*
	 * Function to Get List of Tickets owned by user identified by username.
	 * Can Throw UserNotFoundException
	 * On Success Returns list of Tickets.
	 * */
	
	@Override
	public Set<Ticket> getTickets(String username) throws Exception {
		User user = this.userService.findById(username);
		return ticketRepo.getTickets(user);

	}
	
	/*
	 * Function Find Ticket Using id
	 * Can throw Ticket No Found Exception
	 * On Success Returns ticket object.
	 * 
	 * */
	
	@Override
	public Ticket findTicketById(int ticketId) throws Exception {
		Ticket ticket = this.ticketRepo.findById(ticketId);
		if(ticket == null) {
			logger.error("The given ticket with id: " + ticketId + " not Found ");
			throw new NotFound("The given ticket with id: " + ticketId + " not Found ");
		}
		return ticket;
	}
}
