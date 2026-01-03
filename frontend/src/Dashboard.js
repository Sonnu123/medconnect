import React, { useState, useEffect } from 'react';
import { appointmentService, healthRecordService } from './api';

function Dashboard({ user }) {
  const isPatient = user.role === 'PATIENT';
  const [activeTab, setActiveTab] = useState('appointments');
  const [appointments, setAppointments] = useState([]);
  const [healthRecords, setHealthRecords] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [selectedAppointment, setSelectedAppointment] = useState(null);
  const [notes, setNotes] = useState('');

  const [appointmentForm, setAppointmentForm] = useState({
    doctorId: '',
    appointmentTime: '',
    reason: '',
  });

  const [recordForm, setRecordForm] = useState({
    title: '',
    description: '',
    type: 'LAB_RESULT',
    file: null,
  });

  useEffect(() => {
    loadAppointments();
    if (isPatient) {
      loadHealthRecords();
      loadDoctors();
    }
  }, []);

  const loadAppointments = async () => {
    try {
      const response = isPatient
        ? await appointmentService.getPatientAppointments()
        : await appointmentService.getDoctorAppointments();
      setAppointments(response.data);
    } catch (err) {
      console.error('Error loading appointments:', err);
    }
  };

  const loadHealthRecords = async () => {
    try {
      const response = await healthRecordService.getRecords();
      setHealthRecords(response.data);
    } catch (err) {
      console.error('Error loading health records:', err);
    }
  };

  const loadDoctors = async () => {
    try {
      const response = await appointmentService.getDoctors();
      setDoctors(response.data);
    } catch (err) {
      console.error('Error loading doctors:', err);
    }
  };

  const handleAppointmentSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await appointmentService.create(appointmentForm);
      setMessage({ type: 'success', text: 'Appointment booked successfully!' });
      setShowModal(null);
      setAppointmentForm({ doctorId: '', appointmentTime: '', reason: '' });
      loadAppointments();
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to book appointment' });
    } finally {
      setLoading(false);
    }
  };

  const handleRecordSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('title', recordForm.title);
      formData.append('description', recordForm.description);
      formData.append('type', recordForm.type);
      if (recordForm.file) {
        formData.append('file', recordForm.file);
      }

      await healthRecordService.create(formData);
      setMessage({ type: 'success', text: 'Health record added successfully!' });
      setShowModal(null);
      setRecordForm({ title: '', description: '', type: 'LAB_RESULT', file: null });
      loadHealthRecords();
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to add health record' });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (id, status) => {
    setLoading(true);
    try {
      await appointmentService.updateStatus(id, status, notes);
      setMessage({ type: 'success', text: `Appointment ${status.toLowerCase()} successfully!` });
      setSelectedAppointment(null);
      setNotes('');
      loadAppointments();
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to update appointment' });
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const upcomingAppointments = appointments.filter(
    (apt) => apt.status === 'SCHEDULED' && new Date(apt.appointmentTime) > new Date()
  );

  const pastAppointments = appointments.filter(
    (apt) => apt.status !== 'SCHEDULED' || new Date(apt.appointmentTime) <= new Date()
  );

  return (
    <div className="container">
      <div className="dashboard-header">
        <h2>{isPatient ? 'Patient Dashboard' : 'Doctor Dashboard'}</h2>
      </div>

      {message.text && (
        <div className={message.type === 'success' ? 'success-message' : 'error-message'}>
          {message.text}
        </div>
      )}

      {isPatient ? (
        <>
          <div className="tabs">
            <button
              className={`tab ${activeTab === 'appointments' ? 'active' : ''}`}
              onClick={() => setActiveTab('appointments')}
            >
              My Appointments
            </button>
            <button
              className={`tab ${activeTab === 'records' ? 'active' : ''}`}
              onClick={() => setActiveTab('records')}
            >
              Health Records
            </button>
          </div>

          {activeTab === 'appointments' && (
            <div>
              <button onClick={() => setShowModal('appointment')} className="btn btn-primary">
                Book New Appointment
              </button>

              <div className="grid" style={{ marginTop: '1rem' }}>
                {appointments.map((apt) => (
                  <div key={apt.id} className="appointment-item">
                    <h4>Dr. {apt.doctor.fullName}</h4>
                    <p><strong>Specialization:</strong> {apt.doctor.specialization}</p>
                    <p><strong>Date & Time:</strong> {formatDate(apt.appointmentTime)}</p>
                    <p><strong>Reason:</strong> {apt.reason}</p>
                    <p>
                      <span className={`status-badge status-${apt.status.toLowerCase()}`}>
                        {apt.status}
                      </span>
                    </p>
                    {apt.notes && <p><strong>Notes:</strong> {apt.notes}</p>}
                  </div>
                ))}
                {appointments.length === 0 && <p>No appointments found.</p>}
              </div>
            </div>
          )}

          {activeTab === 'records' && (
            <div>
              <button onClick={() => setShowModal('record')} className="btn btn-primary">
                Add Health Record
              </button>

              <div className="grid" style={{ marginTop: '1rem' }}>
                {healthRecords.map((record) => (
                  <div key={record.id} className="record-item">
                    <h4>{record.title}</h4>
                    <p><strong>Type:</strong> {record.type.replace('_', ' ')}</p>
                    <p><strong>Date:</strong> {formatDate(record.recordDate)}</p>
                    <p>{record.description}</p>
                    {record.fileName && <p><strong>File:</strong> {record.fileName}</p>}
                  </div>
                ))}
                {healthRecords.length === 0 && <p>No health records found.</p>}
              </div>
            </div>
          )}
        </>
      ) : (
        <>
          <div className="card">
            <h3>Upcoming Appointments ({upcomingAppointments.length})</h3>
            <div className="grid">
              {upcomingAppointments.map((apt) => (
                <div key={apt.id} className="appointment-item">
                  <h4>{apt.patient.fullName}</h4>
                  <p><strong>Date & Time:</strong> {formatDate(apt.appointmentTime)}</p>
                  <p><strong>Reason:</strong> {apt.reason}</p>
                  <p><strong>Contact:</strong> {apt.patient.phone}</p>
                  <div style={{ marginTop: '0.5rem', display: 'flex', gap: '0.5rem' }}>
                    <button onClick={() => setSelectedAppointment(apt)} className="btn btn-success">
                      Complete
                    </button>
                    <button
                      onClick={() => handleUpdateStatus(apt.id, 'CANCELLED', '')}
                      className="btn btn-danger"
                      disabled={loading}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ))}
              {upcomingAppointments.length === 0 && <p>No upcoming appointments.</p>}
            </div>
          </div>

          <div className="card">
            <h3>Past Appointments</h3>
            <div className="grid">
              {pastAppointments.map((apt) => (
                <div key={apt.id} className="appointment-item">
                  <h4>{apt.patient.fullName}</h4>
                  <p><strong>Date & Time:</strong> {formatDate(apt.appointmentTime)}</p>
                  <p><strong>Reason:</strong> {apt.reason}</p>
                  <p>
                    <span className={`status-badge status-${apt.status.toLowerCase()}`}>
                      {apt.status}
                    </span>
                  </p>
                  {apt.notes && <p><strong>Notes:</strong> {apt.notes}</p>}
                </div>
              ))}
              {pastAppointments.length === 0 && <p>No past appointments.</p>}
            </div>
          </div>
        </>
      )}

      {showModal === 'appointment' && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Book Appointment</h3>
              <button onClick={() => setShowModal(null)} className="close-btn">×</button>
            </div>
            <form onSubmit={handleAppointmentSubmit}>
              <div className="form-group">
                <label>Doctor</label>
                <select
                  value={appointmentForm.doctorId}
                  onChange={(e) => setAppointmentForm({ ...appointmentForm, doctorId: e.target.value })}
                  required
                >
                  <option value="">Select a doctor</option>
                  {doctors.map((doctor) => (
                    <option key={doctor.id} value={doctor.id}>
                      Dr. {doctor.fullName} - {doctor.specialization}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Date & Time</label>
                <input
                  type="datetime-local"
                  value={appointmentForm.appointmentTime}
                  onChange={(e) => setAppointmentForm({ ...appointmentForm, appointmentTime: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Reason for Visit</label>
                <textarea
                  value={appointmentForm.reason}
                  onChange={(e) => setAppointmentForm({ ...appointmentForm, reason: e.target.value })}
                  rows="3"
                  required
                />
              </div>
              <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
                {loading ? 'Booking...' : 'Book Appointment'}
              </button>
            </form>
          </div>
        </div>
      )}

      {showModal === 'record' && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Add Health Record</h3>
              <button onClick={() => setShowModal(null)} className="close-btn">×</button>
            </div>
            <form onSubmit={handleRecordSubmit}>
              <div className="form-group">
                <label>Title</label>
                <input
                  type="text"
                  value={recordForm.title}
                  onChange={(e) => setRecordForm({ ...recordForm, title: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Type</label>
                <select
                  value={recordForm.type}
                  onChange={(e) => setRecordForm({ ...recordForm, type: e.target.value })}
                >
                  <option value="LAB_RESULT">Lab Result</option>
                  <option value="PRESCRIPTION">Prescription</option>
                  <option value="XRAY">X-Ray</option>
                  <option value="REPORT">Report</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={recordForm.description}
                  onChange={(e) => setRecordForm({ ...recordForm, description: e.target.value })}
                  rows="3"
                />
              </div>
              <div className="form-group">
                <label>Upload File (Optional)</label>
                <input
                  type="file"
                  onChange={(e) => setRecordForm({ ...recordForm, file: e.target.files[0] })}
                />
              </div>
              <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
                {loading ? 'Adding...' : 'Add Record'}
              </button>
            </form>
          </div>
        </div>
      )}

      {selectedAppointment && (
        <div className="modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Complete Appointment</h3>
              <button onClick={() => setSelectedAppointment(null)} className="close-btn">×</button>
            </div>
            <p><strong>Patient:</strong> {selectedAppointment.patient.fullName}</p>
            <p><strong>Reason:</strong> {selectedAppointment.reason}</p>
            <div className="form-group" style={{ marginTop: '1rem' }}>
              <label>Clinical Notes</label>
              <textarea
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                rows="5"
                placeholder="Enter clinical notes, diagnosis, treatment plan, etc."
              />
            </div>
            <button
              onClick={() => handleUpdateStatus(selectedAppointment.id, 'COMPLETED')}
              className="btn btn-success"
              disabled={loading}
              style={{ width: '100%' }}
            >
              {loading ? 'Completing...' : 'Mark as Completed'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
